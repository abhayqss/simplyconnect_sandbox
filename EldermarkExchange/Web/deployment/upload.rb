#!/usr/bin/env ruby

require 'net/ssh'
require 'net/sftp'
require 'highline/import'
require 'trollop'

STDOUT.sync = true

opts = Trollop::options do
  opt :password, "Password to deployment host", :type => :string
  opt :dropDb, "If true database will be dropped", :default => false
  opt :appendTestData, "If true test data will be inserted to database", :default => true
  opt :env, "Deployment environment (pre|prod) default=pre", :type => :string, :default => 'pre'
  opt :server, "Deployment server (mts|cti) default=cti", :type => :string, :default => 'cti'
  opt :help, "Usage: "\
  "deployer_mysql.rb --server=(mts|cti) --env=(pre|prod)"
end

server = opts[:server]
env = opts[:env]

host = "207.250.113.236"
user = "pciadmin"
port = "22"
jetty_suffix = ''


puts "Start SSH session to host #{host} (#{server}) with environment #{env} from user #{user}"

password = password || ask("Enter password for user: ") { |q| q.echo = "*" }

Net::SFTP.start(host, user, :password => password, :port => port) do |sftp|
  puts "Start uploading WAR"
  sftp.upload!("./../target/exchange_rba.war", "/home/pciadmin/exchange_rba.war")
end

Net::SSH.start(host, user, :password => password, :port => port) do |ssh|

  'puts "Stop JETTY"
  puts "Copy WAR to TOMCAT webapps"
  puts = ssh.exec!("cp -rf /home/pciadmin/exchange_rba.war /var/lib/tomcat6/webapps/")
  
  puts "restart tomcat"
  puts = ssh.exec!("/etc/init.d/tomcat6 restart")'
  puts "uploaded"
	

  user = gets.chomp

end
