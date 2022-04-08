package grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private Connection cn;
    private final String path = "db/scripts/post.sql";

    private void initConnection() {
        try (InputStream in = PsqlStore.class
                .getClassLoader()
                .getResourceAsStream("post.properties")
        ) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            LOG.error("Connection initialization", e);
        }
    }

    private void createTab() {
        try (Statement statement = cn.createStatement()) {
            String sql = new String(Files.readAllBytes(Paths.get(path)));
            statement.execute(sql);
        } catch (Exception e) {
            LOG.error("Sql/IO exception", e);
        }
    }

    @Override
    public Post save(Post post) {
        try (PreparedStatement ps = cn.prepareStatement(
                "insert into items (name, link, description, created) values (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getLink());
            ps.setString(3, post.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.execute();
            LOG.info("Post '{}' added successfully", post.getTitle());
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("SQL error while post addition", e);
        }
        return post;
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(
                "Select * from post"
        )) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(getDbPost(resultSet));
                }
            }
        } catch (SQLException e) {
            LOG.error("Error", e);
        }
        return null;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement ps = cn.prepareStatement(
            "Select * from post where id = ?"
        )) {
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    post = getDbPost(resultSet);
                }
            }
        } catch (SQLException e) {
            LOG.error("SqlException", e);
        }
        return post;
    }

    @Override
    public List<Post> findBySubString(String subString) {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(
                "select * from post where name like '%?%'"
        )) {
            ps.setString(1, subString);
            try (ResultSet resultSet = ps.executeQuery()) {
                posts.add(getDbPost(resultSet));
            }
        } catch (SQLException e) {
            LOG.error("SqlException", e);
        }
        return posts;
    }

    @Override
    public void close() throws Exception {
        if (cn != null)  {
            cn.close();
        }
    }

    private Post getDbPost(ResultSet resultSet) {
        Post post = new Post();
        try {
            post.setId(resultSet.getInt("id"));
            post.setTitle(resultSet.getString("name"));
            post.setLink(resultSet.getString("link"));
            post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
        } catch (SQLException e) {
            LOG.error("SqlException", e);
        }
        return post;
    }
}
