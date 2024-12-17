import {useContext, useEffect} from "react";
import {AuthContext} from "../components/AuthProvider.jsx";
import {useNavigate} from "react-router-dom";

const ExitPage = () => {
    const {doLogout} = useContext(AuthContext)

    const navigate = useNavigate();

    useEffect(() => {
        doLogout()
        navigate("/discussions")
    }, [doLogout, navigate])
    return (
        <></>
    );
};

export default ExitPage;