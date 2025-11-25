import { Stack } from "expo-router";

export default function AuthRootLayout() {

  return (
    <Stack>
      <Stack.Screen name="cadastro" options={{ title: "Cadastro de Usuário", headerShown: false }} />
      <Stack.Screen name="login" options={{ title: "Login de Usuário", headerShown: false }} />
    </Stack>
  )
}