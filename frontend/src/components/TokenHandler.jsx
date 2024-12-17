import {useContext, useEffect} from "react";
import {AuthContext} from "./AuthProvider.jsx";
import {useNavigate} from "react-router-dom";

const Token = () => {
    const {doLogin} = useContext(AuthContext);
    const navigate = useNavigate()

    useEffect(() => {
        const hash = window.location.hash;
        const params = new URLSearchParams(hash.substring(1));
        const token = params.get('access_token');

        if (token) {
            fetch(`${import.meta.env.VITE_BACKEND_API}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    token: token,
                })
            })
                .then(res => {
                    if(res.ok) {
                        doLogin(token)
                    }
                })
                .catch(error => {
                    console.error('Fetch error:', error);
                });
        }

        navigate("/discussions")
    }, [doLogin, navigate]);

    return (
        <></>
    );
};

export default Token;