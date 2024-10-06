import React from 'react'

export type ProfileProps = {
    name: string
}

function Profile(props: ProfileProps) {
  return (
    <div>
      Private profile component: {props.name}
    </div>
  )
}

export default Profile
