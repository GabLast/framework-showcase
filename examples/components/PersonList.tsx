import { Name } from "./Person.type"

type PersonListProps = {
    // names: {
    //     first: string
    //     last: string
    // }[]
    names: Name[]
}

function PersonList(props: PersonListProps) {
  return (
    <div>
      {
        props.names.map(it => {
            return(
                <h5>{it.first} {it.last}</h5>
            )
        })
      }
    </div>
  )
}

export default PersonList
