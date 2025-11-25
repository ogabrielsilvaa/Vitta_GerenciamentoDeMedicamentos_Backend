import { Asset } from "expo-asset";
import {
  router,
  Stack,
  useRootNavigationState,
  useSegments,
} from "expo-router";
import * as SplashScreen from "expo-splash-screen";
import React, { useEffect, useState } from "react";
import { Image, StyleSheet, View } from "react-native";
import { AuthProvider, useSession } from "../contexts/authContext";

SplashScreen.preventAutoHideAsync();

const logo = require("../assets/images/logo_vitta.png");

function RootLayoutNav() {
  const { session, isLoading } = useSession();
  const segments = useSegments();
  const navigationState = useRootNavigationState();

  const [isAppReady, setIsAppReady] = useState(false);
  const [animationFinished, setAnimationFinished] = useState(false);

  useEffect(() => {
    async function prepare() {
      try {
        await Asset.fromModule(logo).downloadAsync();
      } catch (e) {
        console.warn(e);
      } finally {
        setIsAppReady(true);
        await SplashScreen.hideAsync();
      }
    }

    prepare();
  }, []);

  useEffect(() => {
    if (!isAppReady || isLoading || !navigationState?.key) return;

    const segmentsArray = segments as string[];
    const inAuthGroup = segmentsArray[0] === "(auth)";
    const atIndex = segmentsArray.length === 0 || segmentsArray[0] === "index";
    const inPublicArea = atIndex || inAuthGroup;

    if (session && inPublicArea) {
      router.replace("/(tabs)/home");
    } else if (!session && !inPublicArea) {
      router.replace("/");
    }

    setTimeout(() => {
      setAnimationFinished(true);
    }, 1500);
  }, [session, isLoading, isAppReady, segments, navigationState?.key]);

  if (!isAppReady || !animationFinished) {
    return (
      <View style={styles.splashContainer}>
        <Image source={logo} style={styles.logo} resizeMode="contain" />
      </View>
    );
  }

  return (
    <Stack>
      <Stack.Screen name="index" options={{ headerShown: false }} />
      <Stack.Screen name="(auth)" options={{ headerShown: false }} />
      <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
      <Stack.Screen
        name="perfil"
        options={{
          title: "Meu Perfil",
          headerShown: true,
          headerBackTitle: "Voltar",
          headerTintColor: '#1CBDCF',
        }}
      />
    </Stack>
  );
}

export default function RootLayout() {
  return (
    <AuthProvider>
      <RootLayoutNav />
    </AuthProvider>
  );
}

const styles = StyleSheet.create({
  splashContainer: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: "center",
    alignItems: "center",
  },
  logo: {
    width: 200,
    height: 200,
  },
});
