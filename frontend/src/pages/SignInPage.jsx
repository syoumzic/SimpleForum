const SignInPage = () => {
    const response_type = 'token'

    return (
        <div className="signup-area">
            <div className="discussion-item">
                <div className="discussion-info">
                    <label>Выберите способы удалённого входа</label>
                    <a className="yandex-button"
                       href={`https://oauth.yandex.ru/authorize?response_type=${response_type}&client_id=${import.meta.env.VITE_CLIENT_ID}&redirect_uri=${import.meta.env.VITE_REDIRECT_URL}`}>
                        <img className="yandex-icon" src='/src/assets/yandex_icon.png'/>
                    </a>
                </div>
            </div>
        </div>
    );
};

export default SignInPage;