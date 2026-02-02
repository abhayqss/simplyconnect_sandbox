package com.scnsoft.eldermark.monitor;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DataSyncMonitorJob implements Job {

    private String email;
    private int interval;
    private String dbUsername;
    private String dbPassword;
    private String dbUrl;

    private static final String SUCCESS_MSG = "Successfully synchronized database";
    private static final long ONE_MINUTE_IN_MILLIS = 60000;

    private static Logger logger = LoggerFactory.getLogger(DataSyncMonitorJob.class);

    private static final String MAIL_STORE_PROTOCOL = "pop3";
    private static final String MAIL_TRANSPORT_PROTOCOL = "smtp";
    private String mailLogin;
    private String mailHost;
    private String mailPassword;
    private String platform;
    private String mailDebug;
    private int mailPort;
    private String mailHostIp;

    private int attachLogsInterval;
    private String pathToLogFile;
    private String listOfIgnoredDatabaseNames;

    public DataSyncMonitorJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Map<Integer, String> dbMap = new TreeMap<Integer, String>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            stmt = conn.createStatement();
            String sql = "SELECT [id], [name] FROM [SourceDatabase] WHERE [remote_host] IS NOT NULL ";

            if(!listOfIgnoredDatabaseNames.isEmpty()) {
                sql += " AND [name] NOT IN (" + listOfIgnoredDatabaseNames + ")";
            }
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                dbMap.put(rs.getInt("id"), rs.getString("name"));
            }
            rs.close();
            stmt.close();

            Date currentDate = new Date();
            long currentTime = currentDate.getTime();
            long intervalTime = currentTime - interval * ONE_MINUTE_IN_MILLIS;

            pstmt = conn.prepareStatement("SELECT TOP 1 [date] FROM [DataSyncLog] ORDER BY [id] DESC ");
            rs = pstmt.executeQuery();

            Timestamp lastDataSyncLog = null;
            boolean attachLogs = false;
            if(rs.next()) {
                lastDataSyncLog = rs.getTimestamp("date");
                long diffInTime = currentTime - lastDataSyncLog.getTime();
                if(diffInTime > attachLogsInterval * ONE_MINUTE_IN_MILLIS) {
                    attachLogs = true;
                }
            }
            rs.close();

            pstmt = conn.prepareStatement("SELECT [description], [database_id] FROM [DataSyncLog] WHERE [date] BETWEEN ? AND ? ");
            pstmt.setTimestamp(1, new Timestamp(intervalTime));
            pstmt.setTimestamp(2, new java.sql.Timestamp(currentTime));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                String description = rs.getString("description");
                int databaseId = rs.getInt("database_id");
                if (description.contains(SUCCESS_MSG)) {
                    dbMap.remove(new Integer(databaseId));
                }
            }
            rs.close();

            if (!dbMap.isEmpty()) {
                String lineSeparator = System.getProperty("line.separator");

                String from = new SimpleDateFormat("MM-dd HH:mm").format(new Date(intervalTime));
                String to = new SimpleDateFormat("MM-dd HH:mm").format(currentDate);

                StringBuilder msg = new StringBuilder();
                if(attachLogs) {
                    String lastLog = new SimpleDateFormat("MM-dd HH:mm").format(lastDataSyncLog);

                    msg.append(String.format("The latest DataSync activity logged into the Exchange database took place %s.", lastLog));
                    msg.append(lineSeparator);
                    msg.append(lineSeparator);
                    msg.append("DataSync logs are in the attachment.");
                    msg.append(lineSeparator);
                    msg.append("Please, review logs and consider the option of manual restart of the service, since service could be stopped.");
                    msg.append(lineSeparator);
                    msg.append(lineSeparator);
                }

                msg.append(String.format("Following databases were not being synced on the %s platform from %s to %s:", platform, from, to));
                msg.append(lineSeparator);

                boolean first = true;
                String postfix = "";
                for (Map.Entry<Integer, String> entry : dbMap.entrySet()) {
                    msg.append(String.format("%s  id %s, '%s'", postfix, entry.getKey(), entry.getValue()));

                    if(first) {
                        postfix = ";" + lineSeparator;
                        first = false;
                    }
                }
                msg.append(".");
                msg.append(lineSeparator);

                if(!listOfIgnoredDatabaseNames.isEmpty()) {
                    msg.append(lineSeparator);
                    msg.append("* Ignored databases: " + listOfIgnoredDatabaseNames + ".");
                }

                logger.warn(msg.toString());

                sendMessage(msg.toString(), attachLogs);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            try {
                sendMessage(e.getMessage(), true);
            } catch (MessagingException e1) {
                logger.error(e1.getMessage());
            }
            JobExecutionException e2 =
                    new JobExecutionException(e);
            throw e2;
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }


    private void sendMessage(String msg, boolean attachLogs) throws MessagingException {
        Session session = initSession();

        MimeMessage message = new MimeMessage(session);

        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

        message.setFrom(new InternetAddress(mailLogin + "@" + mailHost));

        message.setSubject("Exchange " + platform + ": Sync Error");

        Multipart multipart = new MimeMultipart();

        MimeBodyPart messageBody = new MimeBodyPart();
        messageBody.setText(msg);
        multipart.addBodyPart(messageBody);

        if(attachLogs) {
            try {
                MimeBodyPart messageAttachment = new MimeBodyPart();
                DataSource source = new FileDataSource(pathToLogFile);
                messageAttachment.setDataHandler(new DataHandler(source));
                messageAttachment.setFileName("datasync.log");
                multipart.addBodyPart(messageAttachment);
            } catch (Exception e) {
                logger.warn("DataSync log file was not attached.");
            }
        }

        message.setContent(multipart);

        try {
            Transport.send(message);
        } catch (SendFailedException e) {
            logger.error(e.getMessage());
            javax.mail.Address[] invalidAddresses = e.getInvalidAddresses();
//            if (invalidAddresses!=null && invalidAddresses.length>0) {
//                logger.error("Invalid Addresses");
//            }
            if (invalidAddresses != null) {
                for (Address address : invalidAddresses) {
                    logger.error(address.toString());
                }
            }
            javax.mail.Address[] validUnsentAddresses = e.getValidUnsentAddresses();
            message.setRecipients(Message.RecipientType.TO, validUnsentAddresses);
            Transport.send(message);
        }
    }

    private Session initSession() {
        Properties props = new Properties();
        props.put("mail.host", mailHostIp);
        props.put("mail.store.protocol", MAIL_STORE_PROTOCOL);
        props.put("mail.transport.protocol", MAIL_TRANSPORT_PROTOCOL);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", mailPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", mailDebug);
        //props.put("mail.debug", "true");


        return Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailLogin, mailPassword);
                    }
                });
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setMailLogin(String mailLogin) {
        this.mailLogin = mailLogin;
    }

    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public void setMailPort(int mailPort) {
        this.mailPort = mailPort;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setMailDebug(String mailDebug) {
        this.mailDebug = mailDebug;
    }

    public void setMailHostIp(String mailHostIp) {
        this.mailHostIp = mailHostIp;
    }

    public void setPathToLogFile(String pathToLogFile) {
        this.pathToLogFile = pathToLogFile;
    }

    public void setListOfIgnoredDatabaseNames(String listOfIgnoredDatabaseNames) {
        this.listOfIgnoredDatabaseNames = listOfIgnoredDatabaseNames;
    }

    public void setAttachLogsInterval(int attachLogsInterval) {
        this.attachLogsInterval = attachLogsInterval;
    }
}
