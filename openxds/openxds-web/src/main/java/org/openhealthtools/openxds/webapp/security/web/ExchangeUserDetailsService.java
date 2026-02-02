package org.openhealthtools.openxds.webapp.security.web;

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

public class ExchangeUserDetailsService extends JdbcDaoSupport implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException, DataAccessException
    {
        String[] split = input.split(ExtraParamAuthenticationFilter.delimiter);
        if(split.length < 2) {
            throw new UsernameNotFoundException("Both username and company must be selected");
        }

        String username = split[0];
        String companyId = split[1];

        List<ExchangeUser> users = findByUsernameAndCompany(username, companyId);
        if(users.isEmpty()) {
            throw new UsernameNotFoundException(String.format("No users found [%s]", input));
        } else if (users.size() > 1) {
            throw new UsernameNotFoundException(String.format("More than one user [%s]", input));
        }

        ExchangeUser user = users.get(0); // contains no GrantedAuthority[]

        Set<GrantedAuthority> authorities = new HashSet<>(loadUserRole(user.getId()));

        return new ExchangeUser(user.getId(),
                             user.getUsername(),
                             user.getCompanyId(),
                             user.getPassword(),
                             user.isEnabled(), true, true, true,
                             authorities);
    }


    private List<ExchangeUser> findByUsernameAndCompany(String username, String companyId) {
        String sql= "SELECT [id], [login], [password], CASE [inactive] WHEN 1 THEN 'false' ELSE 'true' END AS 'enabled', e.[database_id] " +
                    "   FROM [dbo].[Employee] e " +
                    "   INNER JOIN [dbo].[SystemSetup] s ON e.database_id = s.database_id " +
                    "   WHERE [login] = ? AND UPPER(s.[login_company_id]) = ?";

        return getJdbcTemplate().query(sql, new Object[] {username, companyId.toUpperCase()},new RowMapper<ExchangeUser>() {
            @Override
            public ExchangeUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("id");
                String username = rs.getString("login");
                String password = rs.getString("password");
                Long companyId = rs.getLong("database_id");
                boolean enabled = rs.getBoolean("enabled");
                return new ExchangeUser(id, username, companyId, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
            }
        });
    }

    private List<GrantedAuthority> loadUserRole(long employeeId) {
        String sql =
                "SELECT r.[code] name " +
                        "   FROM [dbo].[Employee] e" +
                        "       JOIN [dbo].[CareTeamRole] r ON e.[care_team_role_id] = r.id " +
                        "   WHERE e.[id] = ?";

        return getJdbcTemplate().query(sql, new Object[] {employeeId}, new RowMapper<GrantedAuthority>() {
            @Override
            public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
                String roleName = rs.getString("name");
                return new SimpleGrantedAuthority(roleName);
            }
        });
    }
}
