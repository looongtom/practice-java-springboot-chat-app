import React, { useContext, useState, useEffect } from "react";
import TextInput from "./TextInput";
import Button from "./Button";
import SocketClientContext from "../context/SocketClientContext";
import backendClient from "../utils/BackendClient";
import "../styles/ChatView.css";
import UserContext from "../context/UserContext";

const ChatView = ({ friend, deliveryStatuses }) => {
  const { connectionId, connectionUsername, convId } = friend;
  const obj = useContext(SocketClientContext);

  const { id: userId } = useContext(UserContext);
  const { socketClient: client } = obj;
  const [messages, setMessages] = useState({});
  const [userMessage, setUserMessage] = useState("");
  const [selectedFile, setSelectedFile] = useState(null);

  if (deliveryStatuses && deliveryStatuses.length > 0) {
    console.dir(deliveryStatuses);
    const keys = Object.keys(messages);
    let fullRefreshObj;
    let statusUpdated = false;
    if (keys) {
      fullRefreshObj = {};
      for (const key of keys) {
        fullRefreshObj[key] = [];
        const messagesOfThisUser = messages[key];
        for (const message of messagesOfThisUser) {
          for (const socketMessageUpdate of deliveryStatuses) {
            if (message.id === socketMessageUpdate.id) {
              if (
                message.messageDeliveryStatusEnum !==
                socketMessageUpdate.messageDeliveryStatusEnum
              ) {
                message.messageDeliveryStatusEnum =
                  socketMessageUpdate.messageDeliveryStatusEnum;

                statusUpdated = true;
              }
            }
          }
          fullRefreshObj[key].push(message);
        }
      }
    }

    if (statusUpdated && fullRefreshObj) {
      setMessages(fullRefreshObj);
    } else {
      console.log("poora loop maara but nothing to update");
    }
  }

  useEffect(() => {
    let subscription = client.subscribe(
      `/topic/${convId}`,
      (message) => {
        const messageBody = JSON.parse(message.body);
        // console.log("from conv id");
        // console.log(messageBody);
        if (
          messageBody.messageType === "CHAT" ||
          messageBody.messageType === "UNSEEN"
        ) {
          setMessages((prev) => {
            const friendsMessages = prev[connectionId] || [];
            const newMessages = [...friendsMessages, messageBody];
            const newObj = { ...prev, [connectionId]: newMessages };
            return newObj;
          });
        }
      },
      "CHAT",
      "UNSEEN"
    );

    const getUnseenMessages = async () => {
      const apiResponse = await backendClient.getUnseenMessages(connectionId);
      setMessages((prev) => {
        const friendsMessages = prev[connectionId] || [];

        const newMessages = [...friendsMessages, ...apiResponse];
        const newObj = { ...prev, [connectionId]: newMessages };
        return newObj;
      });
      backendClient.setReadMessages(apiResponse);
    };

    getUnseenMessages();

    return () => {
      if (subscription) {
        subscription.unsubscribe();
      }
    };
  }, [connectionId, client, convId]);

  const onInputChange = (e) => {
    setUserMessage(e.target.value);
  };

  const onFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  const onSendFile = async () => {
    if (!selectedFile) return;
    try {
      console.log("sending file...........");
      const response = await backendClient.uploadFile(selectedFile);
      console.log("response", response);
      if (response && response.message) {
        console.log("response.message", response.message);
        sendUserMessage(response.message);
      }
      setSelectedFile(null);
    } catch (err) {
      console.error('File upload failed', err);
    }
  };

  const sendUserMessage = (message) => {
    client.publish({
      destination: `/app/chat/sendMessage/${convId}`,
      body: {
        messageType: "CHAT",
        content: message,
        receiverId: connectionId,
        receiverUsername: connectionUsername,
      },
    });
    setUserMessage("");
  };

  return (
    <div>
      <div className="ChatIntroHeader">Chating with {connectionUsername}</div>
      {messages[connectionId] &&
        messages[connectionId].length > 0 &&
        messages[connectionId].map((message, idx) => {
          const isUrl = typeof message.content === 'string' && (message.content.startsWith('http://') || message.content.startsWith('https://'));
          if (message.messageType === "CHAT") {
            if (message.senderId === userId) {
              return (
                <div key={idx}>
                  you:{" "}
                  <span className="MessageDelivery MessageTag">{`(${message.messageDeliveryStatusEnum.toLowerCase()})`}</span>
                  {isUrl ? (
                    <a href={message.content} target="_blank" rel="noopener noreferrer">{message.content}</a>
                  ) : (
                    message.content
                  )}
                </div>
              );
            } else {
              return (
                <div key={idx}>
                  {message.senderUsername}: {isUrl ? (
                    <a href={message.content} target="_blank" rel="noopener noreferrer">{message.content}</a>
                  ) : (
                    message.content
                  )}
                </div>
              );
            }
          } else {
            return (
              <div key={idx}>
                {message.senderUsername}:
                <span className="NewMessage MessageTag">(new)</span>{" "}
                {isUrl ? (
                  <a href={message.content} target="_blank" rel="noopener noreferrer">{message.content}</a>
                ) : (
                  message.content
                )}
              </div>
            );
          }
        })}

      <TextInput
        id="userMessage"
        labelText="Your message: "
        onChange={onInputChange}
        value={userMessage}
      ></TextInput>
      <input type="file" onChange={onFileChange} />
      <Button
        onClick={onSendFile}
        displayText="Send File"
      />
      <Button
        onClick={() => sendUserMessage(userMessage)}
        displayText="Send!"
      />
    </div>
  );
};

export default ChatView;
