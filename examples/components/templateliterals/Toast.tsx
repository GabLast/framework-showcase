type HorizontalPosition = 'left' | 'center' | 'right'
type VerticalPosition = 'top' | 'center' | 'bottom'

type ToastProps = {
    position: Exclude<`${HorizontalPosition}-${VerticalPosition}`,'center-center'> | 'center'
}

function Toast(props: ToastProps) {
  return (
    <div>
      Toast Notification: {props.position}
    </div>
  )
}

export default Toast
