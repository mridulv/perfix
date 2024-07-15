/* eslint-disable react-hooks/rules-of-hooks */
import { useEffect, useRef, useState } from "react";
import { FaArrowUp, FaRegStopCircle } from "react-icons/fa";
import { Navigate, useParams } from "react-router-dom";
import axiosApi from "../../../api/axios";
import { useQuery } from "react-query";
import Loading from "../../../components/Common/Loading";

const ChatPage = () => {
  const [inputValue, setInputValue] = useState("");
  const [isSending, setIsSending] = useState(false);
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

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!inputValue.trim() || isSending) return;

    setIsSending(true);
    const tempMessage = { user: "user", message: inputValue };

    try {
      setInputValue("");
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
    setInputValue(event.target.value);
  };

  useEffect(() => {
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
      {messages.map((message, index) => (
        <div 
          key={index} 
          className={`chat ${message.user === "user" ? "chat-end" : "chat-start"} my-4`}
        >
          <div 
            className={`chat-bubble text-sm ${
              message.user === "user" 
                ? "bg-primary text-white" 
                : "bg-gray-200 text-gray-800"
            }`}
          >
            {message.message}
          </div>
        </div>
      ))}
      {isSending && (
        <div className="chat chat-start my-2" ref={loadingRef}>
          <div className="chat-bubble bg-gray-300 text-gray-800">
          <span className="loading loading-dots loading-md"></span>
          </div>
        </div>
      )}
      <div ref={messagesEndRef} />
    </div>
    <form
      onSubmit={handleSendMessage}
      className="w-full md:w-[65%] mx-auto p-4 bg-secondary border-t border-gray-200 sticky bottom-0"
    >
      <div className="flex items-center">
        <input
          type="text"
          value={inputValue}
          onChange={handleInputChange}
          placeholder="Type a message"
          className="flex-grow p-2 bg-gray-100 rounded-full outline-none focus:ring-2 focus:ring-primary"
          disabled={isSending}
        />
        <button
          type="submit"
          className={`ml-2 p-2 rounded-full ${
            !inputValue || isSending
              ? "bg-gray-300 cursor-not-allowed"
              : "bg-primary hover:bg-primary-dark"
          }`}
          disabled={!inputValue || isSending}
        >
          {
            isSending ? <FaRegStopCircle className="text-primary"/> : <FaArrowUp color="white" />
          }
        </button>
      </div>
    </form>
  </div>
  );
};

export default ChatPage;
