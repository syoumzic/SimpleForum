import {useContext, useEffect, useRef} from "react";
import {AuthContext} from "./AuthProvider.jsx";
import {useNavigate, useParams} from "react-router-dom";
import PropTypes from "prop-types";

const AnswerArea = ({updateAnswerList}) => {
    const { discussionId } = useParams();
    const textValue = useRef("")
    const {isSignIn, token} = useContext(AuthContext);
    const navigate = useNavigate();

    const sendAnswer = () => {
        if(!isSignIn){
            navigate('/login')
            return
        }

        fetch(`${import.meta.env.VITE_BACKEND_API}/putAnswer`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                token: token,
                discussionId: discussionId,
                description: textValue.current
            })
        })
            .then(() => updateAnswerList())


    };

    const handleTextChange = (event) => {
        textValue.current = event.target.value;
    }

    return (
        <div className="discussion-item">
            <div className="discussion-info">
                <div>Ваш ответ</div>
                <div>
                <textarea className="answer-textarea" maxLength="200"
                          onChange={handleTextChange}
                          placeholder="Введите текст"></textarea>
                </div>
                <a className="btn" onClick={() => sendAnswer()}>Отправить</a>
            </div>
        </div>
    );
};

AnswerArea.propTypes = {
    updateAnswerList: PropTypes.func.isRequired,
};

export default AnswerArea;