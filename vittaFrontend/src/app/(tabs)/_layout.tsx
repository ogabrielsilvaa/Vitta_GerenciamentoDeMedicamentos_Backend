import { CustomHeader } from "@/src/components/customHeader";
import { useSession } from "@/src/contexts/authContext";
import { Ionicons } from '@expo/vector-icons';
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { withLayoutContext } from "expo-router";
import { useColorScheme } from "react-native";
import { styles } from "./styles";

export const Tabs = withLayoutContext(
  createBottomTabNavigator().Navigator
);

export default function TabsRootLayout() {
  const { isLoading } = useSession();
  const colorScheme = useColorScheme();

  if(isLoading) {
    return null;
  }

  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: '#1CBDCF',
        tabBarInactiveTintColor: 'gray',
        header: () => <CustomHeader />,
        tabBarStyle: styles.bottomTab,
      }}
    >
      <Tabs.Screen
        name="medicamentos"
        options={{
          title: 'Medicamentos',
          tabBarIcon: ({ color, focused }: { color: string, focused: boolean }) => (
            <Ionicons
              name={focused ? 'medkit' : 'medkit-outline'}
              size={28}
              color={color}
            />
          ),
        }}
      />

      <Tabs.Screen
        name="home"
        options={{
          title: 'Home',
          tabBarIcon: ({ color, focused }: { color: string, focused: boolean }) => (
            <Ionicons
              name={focused ? 'home' : 'home-outline'}
              size={28}
              color={color}
            />
          ),
        }}
      />

      <Tabs.Screen
        name="tratamentos"
        options={{
          title: 'Tratamentos',
          tabBarIcon: ({ color, focused }: { color: string, focused: boolean }) => (
            <Ionicons
              name={focused ? 'medical' : 'medical-outline'}
              size={28}
              color={color}
            />
          ),
        }}
      />

    </Tabs>
  )
}