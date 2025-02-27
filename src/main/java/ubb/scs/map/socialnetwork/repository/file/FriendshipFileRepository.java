package ubb.scs.map.socialnetwork.repository.file;

import ubb.scs.map.socialnetwork.domain.Friendship;
import ubb.scs.map.socialnetwork.domain.Tuple;
import ubb.scs.map.socialnetwork.domain.User;

import java.time.LocalDateTime;

public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Friendship> {

    public FriendshipFileRepository(String filename) {
        super(filename);
    }


    public Friendship createEntity(String line) {
        String[] splited = line.split(";");
        return new Friendship(Long.parseLong(splited[0]),Long.parseLong(splited[1]), LocalDateTime.parse(splited[2]));
    }


    public String saveEntity(Friendship friendship) {
        String s = friendship.getId().getE1() + ";" + friendship.getId().getE2() + ";" + friendship.getFriendshipDate();
        return s;
    }
}
