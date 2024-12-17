import {BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";
import "./App.css";
import DiscussionPage from "./pages/DiscussionPage.jsx";
import AboutPage from "./pages/AboutPage.jsx";
import NotFoundPage from "./pages/NotFoundPage.jsx";
import AnswerPage from "./pages/AnswerPage.jsx";
import MyDiscussionPage from "./pages/MyDiscussionPage.jsx";
import SignInPage from "./pages/SignInPage.jsx";
import TokenHandler from "./components/TokenHandler.jsx";
import {AuthProvider} from "./components/AuthProvider.jsx";
import ExitPage from "./pages/ExitPage.jsx";

const App = () => {
    return (
        <div className="App">
            <AuthProvider>
                <Router>
                    <Routes>
                        <Route path="/" element={<Navigate to="/discussions" />} />
                        <Route path="/discussions" element={<DiscussionPage />} />
                        <Route path="/mydiscussion" element={<MyDiscussionPage />} />
                        <Route path="/discussion/:discussionId" element={<AnswerPage />} />
                        <Route path="/token" element={<TokenHandler />} />
                        <Route path="/about" element={<AboutPage />} />
                        <Route path="/login" element={<SignInPage />}></Route>
                        <Route path="/exit" element={<ExitPage />}></Route>
                        <Route path="*" element={<NotFoundPage />} />
                    </Routes>
                </Router>
            </AuthProvider>
        </div>
    );
};

export default App;
