import Header from "../components/Header.jsx";
import DiscussionItem from "../components/DiscussionItem.jsx";
import {useEffect, useState} from "react";

const DiscussionPage = () => {

    const [discussions, setDiscussions] = useState([])

    useEffect(() => {
        fetch(`${import.meta.env.VITE_BACKEND_API}/discussions`)
                .then(res => res.json())
                .then(data => setDiscussions(data))
                .catch(err => console.log(err))
        }, []
    )

    return (
        <>
            <Header />
            <main>
                <div className="discussion-list">
                    {discussions.map((discussion, index) => (
                        <DiscussionItem key={index} {...discussion} />
                    ))}
                </div>
            </main>
        </>
    )
}

export default DiscussionPage