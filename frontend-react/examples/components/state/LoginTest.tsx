import React, { useState } from 'react'

function LoginTest() {

    const [isLoggedIn, setLoggedIn] = useState(false)

    const handleLogin = () => {
        setLoggedIn(true)
    }

    const handleLogout = () => {
        setLoggedIn(false)
    }

    return (
        <div>
            <button onClick={handleLogin}>Login</button>
            <button onClick={handleLogout}>Logout</button>
            <h1>Current state: { isLoggedIn ? 'Logged In': 'Logged Out' }</h1>
        </div>
    )
}

export default LoginTest
