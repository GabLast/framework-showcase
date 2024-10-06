import React, { ProfilerProps } from 'react'
import Login from './Login'
import Profile, { ProfileProps } from './Profile'

type PrivateProps = {
    isLogged: boolean
    component: React.ComponentType<ProfileProps>
}

function Private({ isLogged, component: Component }: PrivateProps) {
    if (isLogged) {
        return (
            <Component name='Un nombre'/>
        )
    } else {
        return <Login/>
    }

}

export default Private
