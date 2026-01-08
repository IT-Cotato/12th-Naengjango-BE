import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DbCheckController {

    private final JdbcTemplate jdbcTemplate;

    public DbCheckController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/db-check")
    public List<Map<String, Object>> check() {
        return jdbcTemplate.queryForList("SELECT * FROM deploy_check ORDER BY id DESC LIMIT 20");
    }
}
