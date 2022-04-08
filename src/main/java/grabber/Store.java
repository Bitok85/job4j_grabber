package grabber;

import java.util.List;

public interface Store {

    Post save(Post post);

    List<Post> getAll();

    Post findById(int id);

    List<Post> findBySubString(String subString);
}
