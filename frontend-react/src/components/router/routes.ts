type RouteProps = {
  caption: string;
  icon?: string;
  route: string;
};

export const processesRoutes: RouteProps[] = [
  {
    caption: "Test Data",
    icon: "document_scanner",
    route: "/",
  },
];

export const reportRoutes: RouteProps[] = [
  {
    caption: "Test Data",
    icon: "document_scanner",
    route: "/",
  },
];

export const securityRoutes: RouteProps[] = [
  {
    caption: "Profiles",
    icon: "contacts",
    route: "/",
  },
  {
    caption: "Users",
    icon: "person",
    route: "/",
  },
];
