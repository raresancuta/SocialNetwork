package ubb.scs.map.socialnetwork.repository;


import ubb.scs.map.socialnetwork.domain.Friendship;
import ubb.scs.map.socialnetwork.domain.Tuple;
import ubb.scs.map.socialnetwork.domain.User;

import ubb.scs.map.socialnetwork.utils.paging.Page;
import ubb.scs.map.socialnetwork.utils.paging.Pageable;

public interface FriendshipRepository extends PagingRepository<Tuple<Long,Long>, Friendship> {
    Page<Friendship> findAllFriendsOfUserOnPage(Pageable pageable, User user);
}

