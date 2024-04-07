package edu.sjsu.cmpe272.simpleblog.server.repository;

import edu.sjsu.cmpe272.simpleblog.server.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m where m.msgId <= :msgId order by m.msgId DESC")
    Page<Message> findMessagesWithMaxIdLessThanOrderByMessageIdDescLimit(@Param("msgId") int msgId, Pageable pageable);

    @Query("SELECT m FROM Message m order by m.msgId DESC")
    Page<Message> findMessagesOrderByMessageIdDescLimit(Pageable pageable);

}
