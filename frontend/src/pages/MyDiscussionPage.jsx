import Header from "../components/Header.jsx";
import DiscussionItem from "../components/DiscussionItem.jsx";
import {useContext, useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "../components/AuthProvider.jsx";

const MyDiscussionPage = () => {
    const {isSignIn, token} = useContext(AuthContext);
    const [discussions, setDiscussions] = useState(null);
    const navigate = useNavigate();
    const titleValue = useRef("")
    const descriptionValue = useRef("")

    useEffect(() => {
        updateDiscussion()
    }, []);

    const onUpdateTitle = (event) => {
        titleValue.current = event.target.value;
        console.log(titleValue.current)
    }

    const onUpdateDescription = (event) => {
        descriptionValue.current = event.target.value;
        console.log(descriptionValue.current)
    }

    const sendDiscussion = () => {
        if(!isSignIn){
            navigate('/login')
        }

        fetch(`${import.meta.env.VITE_BACKEND_API}/putDiscussion`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                token: token,
                title: titleValue.current,
                description: descriptionValue.current
            })
        })
            .then(res => updateDiscussion())
            .catch(e => console.log(e));
    }

    const updateDiscussion = () => {
        if (!isSignIn) {
            navigate('/login')
            return
        }

        fetch(`${import.meta.env.VITE_BACKEND_API}/myDiscussion`, {
          method: "POST",
          headers: {
              'Content-Type': 'application/json'
          },
          body: JSON.stringify({
              token: token
          })
        })
            .then(res => res.json())
            .then(data => setDiscussions(data))
            .catch(e => console.log(e));
    }

    return (
        <>
            <Header/>
            <main>
                <div className="discussion-item">
                    <div className="discussion-info">
                        <textarea className="title-area" onChange = {onUpdateTitle} placeholder="Название" autoCorrect="off"></textarea>
                        <textarea className="discussion-area" onChange = {onUpdateDescription} placeholder="Описание"
                                  autoCorrect="off"></textarea>
                        <div>
                            <btn className="btn" onClick={sendDiscussion}>Создать обсуждение</btn>
                        </div>
                    </div>
                </div>
                {
                    discussions != null ?
                        (
                            <div className="discussion-list">
                                {discussions.map((discussion, index) => (
                                    <DiscussionItem key={index} {...discussion} />
                                ))}
                            </div>
                        )
                        :
                        (<></>)
                }
            </main>
        </>
    )
}

export default MyDiscussionPage