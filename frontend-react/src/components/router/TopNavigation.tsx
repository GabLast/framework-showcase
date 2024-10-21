import React from "react";
import {
    Navbar,
    MobileNav,
    Typography,
    Button,
    IconButton,
    Card,
    Avatar,
    Menu,
    MenuHandler,
    MenuList,
    MenuItem,
} from "@material-tailwind/react";
import 'material-icons/iconfont/material-icons.css';
import MyIcon from "../icons/MyIcon";

type TopNavigationProps = {
    action: () => void
}

export const TopNavigation = (props: TopNavigationProps) => {
    const [isNavOpen, setIsNavOpen] = React.useState(false);

    const toggleIsNavOpen = () => setIsNavOpen((cur) => !cur);

    // React.useEffect(() => {
    //     window.addEventListener(
    //         "resize",
    //         () => window.innerWidth >= 960 && setIsNavOpen(false),
    //     );
    // }, []);

    return (
        <Navbar className="mx-auto max-w-screen-xl p-2 lg:pl-6">
            <div className="relative mx-auto flex items-center justify-between text-blue-gray-900">
                <MyIcon name="view_headline" action={props.action} />
                <ProfileMenu/>
            </div>

        </Navbar>
    );
}

function ProfileMenu() {
    const [isMenuOpen, setIsMenuOpen] = React.useState(false);

    const closeMenu = () => setIsMenuOpen(false);

    return (
        <Menu open={isMenuOpen} handler={setIsMenuOpen} placement="bottom-end">
            <MenuHandler>
                <Button
                    variant="text"
                    color="blue-gray"
                    className="flex items-center gap-1 rounded-full py-0.5 pr-2 pl-0.5 lg:ml-auto"
                >
                    <Avatar
                        variant="circular"
                        size="md"
                        alt=""
                        className="border border-gray-900 p-0.5"
                        src="src\assets\blank_avatar.PNG"
                    />
                </Button>
            </MenuHandler>
        </Menu>
    );
}