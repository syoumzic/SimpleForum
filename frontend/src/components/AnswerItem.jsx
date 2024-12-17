import PropTypes from "prop-types";

const AnswerItem = ({description, user }) => {
    return (
        <div className="discussion-item">
            <div className="discussion-info">
                <p className="discussion-description">{description}</p>
                <p className="discussion-author">{user}</p>
            </div>
        </div>
    );
};


AnswerItem.propTypes = {
    description: PropTypes.any.isRequired,
    user: PropTypes.any.isRequired
};

export default AnswerItem;