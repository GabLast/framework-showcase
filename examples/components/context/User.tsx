import { useContext, useState } from "react"
import { UserContext } from "./UserContext"

function User() {

    const userContext = useContext(UserContext)

    const handleLogin = () => {
        userContext.setUser({
            name: 'Jane',
            email: 'mail@mail.com'
        })
    }

    const handleLogout = () => {
        userContext.setUser(null)
    }

    return (
        <div>
            <button onClick={handleLogin}>Context login</button>
            <button onClick={handleLogout}>Context Logout</button>
            <h1>User: {userContext.user?.name} Mail: {userContext.user?.email}</h1>
        </div>
    )
}

export default User
