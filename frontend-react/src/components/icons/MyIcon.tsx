import React from 'react'
import { IconProps } from '../../types/ui/IconProps'

export default function MyIcon(props: IconProps) {
  return (
    <span className="material-icons" onClick={props.action}>
      {props.name}
    </span>
  )
}
