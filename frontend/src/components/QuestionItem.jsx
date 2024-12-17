import PropTypes from "prop-types";

const QuestionItem = ({ title, description, author }) => {
    return (
        <div className="discussion-item">
            <div className="discussion-info">
                <h4 className="discussion-title">{title}</h4>
                <p className="discussion-description">{description}</p>
                <p className="disctiption-author">{author}</p>
            </div>
        </div>
    );
};


QuestionItem.propTypes = {
    title: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    id: PropTypes.string.isRequired,
    author: PropTypes.string.isRequired,
};

export default QuestionItem;