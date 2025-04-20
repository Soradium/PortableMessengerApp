package org.soradium.portablemessengerapp.repository;

import org.soradium.portablemessengerapp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
