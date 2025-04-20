import React, {useEffect, useState} from 'react';
import * as StompJs from '@stomp/stompjs';
import apiClient from './login-related/newApiClient';

const ChatMenu = ({targetUserName}) => {
    const [messages, setMessages] = useState([]); // Ensure messages is initialized as an array
    const [message, setMessage] = useState(''); // Message to send
    const [client, setClient] = useState(null); // WebSocket client

    useEffect(() => {
        // Retrieve messages that are already in the database
        const fetchMessages = async () => {
            try {
                const payload = {
                    requestedToUser: targetUserName, // The target user passed as a prop
                };

                const response = await apiClient.post('/chat/retrieve-messages-per-user', payload);
                console.log('Fetched messages:', response.data);

                // Ensure the response data is an array
                setMessages(Array.isArray(response.data) ? response.data : []);
            } catch (error) {
                console.error('Error fetching messages:', error);
                setMessages([]); // Set to an empty array on error
            }
        };

        // Create SockJS-based STOMP client
        const stompClient = new StompJs.Client({
            brokerURL: 'ws://localhost:8080/my-endpoint',
            debug: (str) => console.log(str),
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('Connected to WebSocket');

                // Subscribe to user-specific topic
                stompClient.subscribe('/user/topics/messages-topic', (message) => {
                    console.log('Received:', message.body);

                    // Parse the message body and add it to the messages array
                    const parsedMessage = JSON.parse(message.body);
                    setMessages((prevMessages) => [...prevMessages, parsedMessage]);
                });
            },
        });

        fetchMessages();
        // Activate the client
        stompClient.activate();
        setClient(stompClient);

        return () => {
            stompClient.deactivate();
        };
    }, [targetUserName]); // Re-run effect when targetUserName changes

    const handleSendMessage = () => {
        if (client && targetUserName && message) {
            const sentMessage = {
                targetUserName: targetUserName,
                message: message,
            };

            client.publish({
                destination: '/app/map',
                headers: {
                    'content-type': 'application/json',
                },
                body: JSON.stringify(sentMessage),
            });

            console.log('Message sent:', sentMessage);
            setMessage(''); // Clear the message input
        } else {
            console.error('Client not connected or missing fields');
        }
    };

    return (
        <div>
            <h2>Chat Menu</h2>
            <div>
                <label>
                    Message:
                    <input
                        type="text"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        placeholder="Enter your message"
                    />
                </label>
            </div>
            <button onClick={handleSendMessage}>Send Message</button>
            <h3>Messages:</h3>
            <ul>
                {/* Ensure messages is an array and render specific properties */}
                {Array.isArray(messages) ? (
                    messages.map((msg, idx) => (
                        <li key={idx}>
                            {/* Render the 'message' property of each object */}
                            {msg.message || 'No message content'}
                        </li>
                    ))
                ) : (
                    <li>No messages available</li>
                )}
            </ul>
        </div>
    );
};

export default ChatMenu;