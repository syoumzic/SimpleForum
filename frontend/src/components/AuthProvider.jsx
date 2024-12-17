import {createContext, useState} from "react";
import PropTypes from "prop-types";

export const AuthContext = createContext(null)

export const AuthProvider = ({children}) => {
    const context = CreateCustomContext()
    return <AuthContext.Provider value={context}>{children}</AuthContext.Provider>
}

const CreateCustomContext = () => {
    const [isSignIn, setIsSignIn] = useState(false)
    const [token, setToken] = useState('')

    const doLogout = () => {
        setIsSignIn(false)
        setToken('')
    }

    const doLogin = (token) => {
        setIsSignIn(true)
        setToken(token)
    }

    return {
        isSignIn: isSignIn,
        token: token,
        doLogin: doLogin,
        doLogout: doLogout
    };
}

AuthProvider.propTypes = {
    children: PropTypes.node.isRequired,
}