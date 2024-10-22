import React from 'react'
import { IconProps } from '../../types/ui/IconProps'
import 'material-icons/iconfont/material-icons.css';

export default function MyIcon(props: IconProps) {
  return (
    <span className="material-icons" onClick={props.action}>
      {props.name}
    </span>
  )
}
