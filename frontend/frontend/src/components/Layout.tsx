import React from "react";
import { Layout as AntLayout, Menu, Button, Dropdown, Avatar } from "antd";
import type { MenuProps } from "antd";
import {
  UserOutlined,
  CalendarOutlined,
  MedicineBoxOutlined,
  LogoutOutlined,
  DashboardOutlined,
  TeamOutlined,
} from "@ant-design/icons";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import type { ReactNode } from "react";

const { Header, Content, Sider } = AntLayout;

interface LayoutProps {
  children: ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const getMenuItems = (): MenuProps["items"] => {
    if (user?.role === "PATIENT") {
      return [
        {
          key: "/dashboard",
          icon: <DashboardOutlined />,
          label: "Dashboard",
        },
        {
          key: "/book-appointment",
          icon: <CalendarOutlined />,
          label: "Book Appointment",
        },
      ];
    }

    if (user?.role === "DOCTOR") {
      return [
        {
          key: "/dashboard",
          icon: <DashboardOutlined />,
          label: "Dashboard",
        },
      ];
    }

    if (user?.role === "ADMIN") {
      return [
        {
          key: "/dashboard",
          icon: <DashboardOutlined />,
          label: "Dashboard",
        },
        {
          key: "/admin/users",
          icon: <TeamOutlined />,
          label: "Manage Users",
        },
        {
          key: "/admin/hospitals",
          icon: <MedicineBoxOutlined />,
          label: "Manage Hospitals",
        },
        {
          key: "/admin/doctors",
          icon: <UserOutlined />,
          label: "Manage Doctors",
        },
      ];
    }

    return [
      {
        key: "/dashboard",
        icon: <DashboardOutlined />,
        label: "Dashboard",
      },
    ];
  };

  const userMenuItems: MenuProps["items"] = [
    {
      key: "profile",
      icon: <UserOutlined />,
      label: "Profile",
    },
    {
      type: "divider",
    },
    {
      key: "logout",
      icon: <LogoutOutlined />,
      label: "Logout",
      danger: true,
    },
  ];

  const handleMenuClick = ({ key }: { key: string }) => {
    if (key === "logout") {
      handleLogout();
    } else if (key === "profile") {
      navigate("/profile");
    } else {
      navigate(key);
    }
  };

  return (
    <AntLayout className="min-h-screen">
      <Sider
        breakpoint="lg"
        collapsedWidth="0"
        className="bg-white shadow-lg"
      >
        <div className="h-16 flex items-center justify-center border-b">
          <h2 className="text-xl font-bold text-indigo-600">
            Hospital System
          </h2>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={getMenuItems()}
          onClick={handleMenuClick}
          className="border-r-0"
        />
      </Sider>
      <AntLayout>
        <Header className="bg-white px-6 flex items-center justify-between shadow-sm">
          <h1 className="text-lg font-semibold text-gray-800">
            {user?.role === "PATIENT" && "Patient Portal"}
            {user?.role === "ADMIN" && "Admin Portal"}
            {user?.role === "DOCTOR" && "Doctor Portal"}
          </h1>
          <Dropdown
            menu={{
              items: userMenuItems,
              onClick: handleMenuClick,
            }}
            placement="bottomRight"
          >
            <Button
              type="text"
              className="flex items-center gap-2 h-auto"
              icon={<Avatar size="small" icon={<UserOutlined />} />}
            >
              <span className="text-gray-700">{user?.email}</span>
            </Button>
          </Dropdown>
        </Header>
        <Content className="p-6 bg-gray-50 min-h-screen">{children}</Content>
      </AntLayout>
    </AntLayout>
  );
};

export default Layout;

