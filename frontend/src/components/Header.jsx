import {AuthContext} from "./AuthProvider.jsx";
import {useContext} from "react";
import {Link} from "react-router-dom";

const Header = () => {
    const {isSignIn} = useContext(AuthContext)

    return (
        <header className="header">
            <div className="nav-logo">
                <Link to="/discussions" className="nav-logo-link">SimpleForum</Link>
            </div>

            <ul id="center" className="nav-links-middle">
                <li id="link1" className="link"><Link to="/discussions">Свежие обсуждения</Link></li>
                <li id="link2" className="link"><Link to="/mydiscussion">Мои обсуждения</Link></li>
                <li id="link3" className="link"><Link to="/about">О сайте</Link></li>
            </ul>

            <div className="nav-links-right">
                {
                    isSignIn ?
                    <Link className="btn" to="/exit">Выход</Link>
                    :
                    <Link className="btn" to="/login">Вход</Link>
                }
            </div>
        </header>
    )
}

export default Header;
