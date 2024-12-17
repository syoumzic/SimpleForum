import PropTypes from "prop-types";
import {Link} from "react-router-dom";

const DiscussionItem = ({ title, description, id, author }) => {
    return (
        <div className="discussion-item">
            <div className="discussion-info">
                <p className="discussion-title">{title}</p>
                <p className="discussion-description">{description}</p>
                <p className="disctiption-author">{author}</p>
                <div>
                    <Link className="btn" to={`/discussion/${id}`}>Читать</Link>
                </div>
            </div>
        </div>
    );
};


DiscussionItem.propTypes = {
    title: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    id: PropTypes.string.isRequired,
    author: PropTypes.string.isRequired,
};

export default DiscussionItem;