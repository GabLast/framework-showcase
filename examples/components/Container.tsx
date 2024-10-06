import React from 'react'

type ContainerProps = {
    styles?: React.CSSProperties,
    children: React.ReactNode,
}

function Container(props: ContainerProps) {
    return (
        <div style={props.styles}>
            {props.children}
        </div>
    )
}

export default Container
