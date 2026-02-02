package com.scnsoft.exchange.audit.security;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JdbcAuditUserDetailsService extends JdbcDaoSupport implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException, DataAccessException
    {
        String[] split = input.split(ExtraParamAuthenticationFilter.delimiter);
        if(split.length < 2) {
            throw new UsernameNotFoundException("Both username and company must be selected");
        }

        String username = split[0];
        String companyId = split[1];

        List<AuditUser> users = findByUsernameAndCompany(username, companyId);
        if(users.isEmpty()) {
            throw new UsernameNotFoundException(String.format("No users found [%s]", input));
        } else if (users.size() > 1) {
            throw new UsernameNotFoundException(String.format("More than one user [%s]", input));
        }

        AuditUser user = users.get(0); // contains no GrantedAuthority[]

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.addAll(loadUserAuthorities(user.getId()));
        authorities.addAll(loadGroupAuthorities(user.getId()));
        authorities.addAll(addCustomAuthorities(user.getId()));

        return new AuditUser(user.getId(),
                             user.getUsername(),
                             user.getCompanyId(),
                             user.getPassword(),
                             user.isEnabled(), true, true, true,
                             authorities);
    }


    private List<AuditUser> findByUsernameAndCompany(String username, String companyId) {
        String sql= "SELECT [id], [login], [password], CASE [inactive] WHEN 1 THEN 'false' ELSE 'true' END AS 'enabled', e.[database_id] " +
                    "   FROM [dbo].[Employee] e " +
                    "   INNER JOIN [dbo].[SystemSetup] s ON e.database_id = s.database_id " +
                    "   WHERE [login] = ? AND UPPER(s.[login_company_id]) = ?";

        return getJdbcTemplate().query(sql, new Object[] {username, companyId.toUpperCase()},new RowMapper<AuditUser>() {
            @Override
            public AuditUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String username = rs.getString("login");
                String password = rs.getString("password");
                Long companyId = rs.getLong("database_id");
                boolean enabled = rs.getBoolean("enabled");
                return new AuditUser(id, username, companyId, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
            }
        });
    }

    protected List<GrantedAuthority> loadUserAuthorities(long employeeId) {
        String sql =
                "SELECT [Role].[name] " +
                        "   FROM [dbo].[Employee_Organization] " +
                        "       JOIN [dbo].[Employee_Organization_Role] ON [Employee_Organization_Role].[employee_organization_id] = [Employee_Organization].[id] " +
                        "       JOIN [dbo].[Role] ON [Employee_Organization_Role].[role_id] = [Role].id " +
                        "   WHERE [Employee_Organization].[employee_id] = ?";

        return getJdbcTemplate().query(sql, new Object[] {employeeId}, new RowMapper<GrantedAuthority>() {
            @Override
            public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
                String roleName = rs.getString("name");
                return new SimpleGrantedAuthority(roleName);
            }
        });
    }

    protected List<GrantedAuthority> loadGroupAuthorities(long employeeId) {
        String sql =
                "SELECT [Role].[name] " +
                "   FROM [dbo].[Employee_Groups] " +
                "       JOIN [dbo].[Groups] ON [Employee_Groups].[group_id] = [Groups].id " +
                "       JOIN [dbo].[Groups_Role] ON [Groups_Role].[group_id] = [Groups].id " +
                "       JOIN [dbo].[Role] ON [Groups_Role].[role_id] = [Role].id " +
                "   WHERE [Employee_Groups].[employee_id] = ?";

        return getJdbcTemplate().query(sql, new Object[] {employeeId}, new RowMapper<GrantedAuthority>() {
            @Override
            public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
                String roleName = rs.getString("name");
                return new SimpleGrantedAuthority(roleName);
            }
        });
    }

    protected List<GrantedAuthority> addCustomAuthorities(long employeeId) {
        String sql =
                "SELECT [Employee].[login], [Role].[name] " +
                "   FROM [dbo].[Employee_Role] " +
                "       JOIN [dbo].[Role] ON [Employee_Role].[role_id] = [Role].id " +
                "       JOIN [dbo].[Employee] ON [Employee_Role].[employee_id] = [Employee].id " +
                "   WHERE [Employee].[id] = ?";

        return getJdbcTemplate().query(sql, new Object[] {employeeId}, new RowMapper<GrantedAuthority>() {
            @Override
            public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
                String roleName = rs.getString("name");
                return new SimpleGrantedAuthority(roleName);
            }
        });
    }
}
