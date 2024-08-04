/* eslint-disable no-unused-vars */
/* eslint-disable react-hooks/rules-of-hooks */
import React, { useEffect, useRef, useState } from "react";
import { FaArrowUp, FaRegStopCircle } from "react-icons/fa";
import { Navigate, useParams } from "react-router-dom";
import axiosApi from "../../../api/axios";
import { useQuery } from "react-query";
import Loading from "../../../components/Common/Loading";
import ReactMarkdown from "react-markdown";
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter";
import { materialDark } from "react-syntax-highlighter/dist/esm/styles/prism";
import ChatBoxLoader from "../../../components/ChatPage/ChatBoxLoader";
import remarkGfm from "remark-gfm";

const ChatPage = () => {
  const [inputValue, setInputValue] = useState("");
  const [isSending, setIsSending] = useState(false);
  const [localMessages, setLocalMessages] = useState([]);
  const [rows, setRows] = useState(1);
  const messagesEndRef = useRef(null);
  const loadingRef = useRef(null);

  const { id } = useParams();
  if (!id) {
    return <Navigate to="/usecases" replace={true} />;
  }

  const {
    data: useCase,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["useCase", id],
    queryFn: async () => {
      const res = await axiosApi.get(`/usecases/${id}`);
      return res.data;
    },
  });

  const messages = useCase?.useCaseDetails?.messages || [];
  const combinedMessages = [...messages, ...localMessages];

  const handleSendMessage = async (e) => {
    e.preventDefault();
    const sanitizedInputValue = inputValue.replace(/\u200B/g, ''); // Replace zero-width spaces
    if (!sanitizedInputValue.trim() || isSending) return;

    const tempMessage = { user: "user", message: sanitizedInputValue };
    setLocalMessages([...localMessages, tempMessage]);
    setInputValue("");
    setRows(1);
    setIsSending(true);

    try {
      await axiosApi.post(`/usecases/${id}/converse`, {
        message: tempMessage.message,
      });
      await refetch();
    } catch (error) {
      console.error("Error sending message:", error);
      // Optionally, handle the error, e.g., showing a notification to the user
    } finally {
      setIsSending(false);
    }
  };

  const handleInputChange = (event) => {
    const textareaLineHeight = 24; // Adjust this value based on your styling
    const previousRows = event.target.rows;
    event.target.rows = 1; // Reset the number of rows to 1

    const currentRows = Math.floor(event.target.scrollHeight / textareaLineHeight);

    if (currentRows === previousRows) {
      event.target.rows = currentRows;
    }

    if (currentRows >= 5) {
      event.target.rows = 5;
      event.target.scrollTop = event.target.scrollHeight;
    }

    setRows(currentRows < 5 ? currentRows : 5);
    setInputValue(event.target.value);
  };

  useEffect(() => {
    if (!isSending) {
      setLocalMessages([]);
    }
    if (isSending) {
      loadingRef.current?.scrollIntoView({ behavior: "smooth" });
    } else {
      messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  }, [useCase, isSending]);

  if (isLoading) return <Loading />;

  return (
    <div className="min-h-screen flex flex-col bg-white">
      <div className="w-full md:w-[65%] bg-accent mx-auto flex-grow p-4 overflow-y-auto">
        {combinedMessages.map((message, index) => (
          <div
            key={index}
            className={`chat ${
              message.user === "user" ? "chat-end" : "chat-start"
            } my-4`}
          >
            <div
              className={`chat-bubble text-sm ${
                message.user === "user"
                  ? "bg-primary text-white"
                  : "bg-gray-200 text-gray-800"
              }`}
            >
              <ReactMarkdown
                remarkPlugins={[remarkGfm]}
                components={{
                  code(props) {
                    const { children, className, ...rest } = props;
                    const match = /language-(\w+)/.exec(className || "");
                    return match ? (
                      <SyntaxHighlighter
                        {...rest}
                        PreTag="div"
                        language={match[1]}
                        style={materialDark}
                      >
                        {String(children).replace(/\n$/, "")}
                      </SyntaxHighlighter>
                    ) : (
                      <code {...rest} className={className}>
                        {children}
                      </code>
                    );
                  },
                }}
              >
                {message.message}
              </ReactMarkdown>
            </div>
          </div>
        ))}
        {isSending && (
          <div className="chat chat-start my-2" ref={loadingRef}>
            <ChatBoxLoader />
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>
      <form
        onSubmit={handleSendMessage}
        className="w-full md:w-[65%] mx-auto p-4 bg-secondary border-t border-gray-200 sticky bottom-0"
      >
        <div className="flex items-end">
         <textarea
            value={inputValue}
            onChange={handleInputChange}
            placeholder="Type a message"
            className="w-full bg-gray-100 p-2 rounded-md outline-none focus:ring-2 focus:ring-primary resize-none"
            rows={rows}
            disabled={isSending}
            style={{ lineHeight: "24px" }} // Adjust this value based on your styling
          />
          <button
            type="submit"
            className={`w-10 h-10 flex items-center justify-center ml-2 p-2 rounded-full ${
              !inputValue || isSending
                ? "bg-gray-300 cursor-not-allowed"
                : "bg-primary hover:bg-primary-dark"
            }`}
            disabled={!inputValue || isSending}
          >
            {isSending ? (
              <FaRegStopCircle className="text-primary" />
            ) : (
              <FaArrowUp color="white" />
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ChatPage;