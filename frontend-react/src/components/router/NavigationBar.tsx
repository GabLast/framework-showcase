import React, { useEffect, useState } from "react";
import {
    Drawer,
    Button,
    Typography,
    IconButton,
    List,
    ListItem,
    ListItemPrefix,
    ListItemSuffix,
    Chip,
    Collapse,
} from "@material-tailwind/react";

import { TopNavigation } from "./TopNavigation";
import MyIcon from "../icons/MyIcon";
import { processesRoutes, securityRoutes } from "./routes";
import { NavItem } from "./NavItem";

type CollapseProps = {
    state: boolean
    icon: string
}

export const NavigationBar = () => {

    const [open, setOpen] = useState(false);
    const openDrawer = () => setOpen(true);
    const closeDrawer = () => setOpen(false);

    const [processesCollapse, setProcessesCollapse] = useState<CollapseProps>({ state: false, icon: "keyboard_arrow_right" });
    const toggleModuleCollapse = () => setProcessesCollapse({ state: !processesCollapse.state, icon: processesCollapse.state ? "keyboard_arrow_right" : "keyboard_arrow_down" });

    const [securityCollapse, setSecurityCollapse] = useState<CollapseProps>({ state: false, icon: "keyboard_arrow_right" });
    const toggleSecurityCollapse = () => setSecurityCollapse({ state: !securityCollapse.state, icon: securityCollapse.state ? "keyboard_arrow_right" : "keyboard_arrow_down" });

    useEffect(() => {
        window.addEventListener(
            "resize",
            () => window.innerWidth >= 960 && closeDrawer,
        );
    }, []);

    return (
        <React.Fragment>
            <TopNavigation action={openDrawer}/>
            <Drawer open={open} onClose={closeDrawer}>
                <div className="grid grid-cols-1 place-content-center">
                    <Typography variant="h5" className="p-3 flex items-center justify-center">
                        Framework Showcase
                    </Typography>
                </div>
                <NavItem caption="Module" action={toggleModuleCollapse} icon="content_paste_search" suffix={processesCollapse.icon}/>
                <Collapse open={processesCollapse.state}>
                    <List>
                        {
                            processesRoutes.map((it) => {
                                return (
                                    <ListItem>
                                        {it.icon ?
                                            <ListItemPrefix>
                                                <MyIcon name={it.icon} />
                                            </ListItemPrefix> : ""
                                        }
                                        {it.caption}
                                    </ListItem>
                                )
                            })
                        }
                    </List>
                </Collapse>
                <NavItem caption="Security" action={toggleSecurityCollapse} icon="format_line_spacing" suffix={securityCollapse.icon}/>
                <Collapse open={securityCollapse.state}>
                    <List>
                        {
                            securityRoutes.map((it) => {
                                return (
                                    <ListItem>
                                        {it.icon ?
                                            <ListItemPrefix>
                                                <MyIcon name={it.icon} />
                                            </ListItemPrefix> : ""
                                        }
                                        {it.caption}
                                    </ListItem>
                                )
                            })
                        }
                    </List>
                </Collapse>
                {/* <div className="grid grid-cols-1 place-content-center">
                    
                    <Typography className="p-3 flex items-center justify-start hover:bg-blue-gray-50 hover:bg-opacity-80 focus:bg-blue-gray-50 focus:bg-opacity-80 active:bg-blue-gray-50 active:bg-opacity-80 hover:text-blue-gray-900 focus:text-blue-gray-900" onClick={toggleModuleCollapse}>
                        Module
                        <MyIcon name={moduleCollapse.icon} />
                    </Typography>
                </div> */}
            </Drawer>
        </React.Fragment>
    )
}
