import Header from "../components/Header.jsx";
import AnswerItem from "../components/AnswerItem.jsx";
import QuestionItem from "../components/QuestionItem.jsx";
import AnswerArea from "../components/AnswerArea.jsx";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";

const AnswerPage = () => {
    const { discussionId } = useParams();
    const [content, setContent] = useState(null);

    const updateAnswerList = () => {
        fetch(`${import.meta.env.VITE_BACKEND_API}/discussion/${discussionId}`)
            .then(res => res.json())
            .then(data => {console.log(data); return data})
            .then(data =>
            {
                const discussion = data[0]
                const answers = data[1]

                setContent({discussion: discussion, answers: answers})
            })
            .catch(err => console.log(err))
    }

    useEffect(() => {
        updateAnswerList()
    }, []);

    return (
        <>
            <Header/>
            <main>
                <div className="discussion-list">
                    {
                        content != null?
                        (
                            <>
                                <QuestionItem {...content.discussion} />
                                {content.answers.map((answer, index) => (
                                    <AnswerItem key={index} {...answer} />
                                ))}
                            </>
                        )
                        :
                        (
                            <></>
                        )
                    }
                    <AnswerArea updateAnswerList={updateAnswerList}/>
                </div>
            </main>
        </>
    )
}

export default AnswerPage