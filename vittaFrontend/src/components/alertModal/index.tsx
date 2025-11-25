import React from 'react';
import { Modal, Pressable, Text, View } from 'react-native';
import { styles } from './styles';

type AlertModalProps = {
  visible: boolean;
  title: string;
  message: string;
  onClose: () => void;
  onConfirm: () => void;
  confirmText?: string;
  cancelText?: string;
  isDestructive?: boolean;
};

export const AlertModal = ({
  visible,
  title,
  message,
  onClose,
  onConfirm,
  confirmText = "Confirmar",
  cancelText = "Cancelar",
  isDestructive = false,
}: AlertModalProps) => {

  const handleConfirm = () => {
    onConfirm();
    onClose();
  };

  return (
    <Modal
      visible={visible}
      transparent={true}
      animationType="fade"
      onRequestClose={onClose}
    >
      <View style={styles.backdrop}>
        
        <View style={styles.alertBox}>
          <Text style={styles.title}>{title}</Text>
          <Text style={styles.message}>{message}</Text>

          <View style={styles.buttonContainer}>
            <Pressable style={styles.button} onPress={onClose}>
              <Text style={styles.buttonText}>{cancelText}</Text>
            </Pressable>

            <Pressable style={styles.button} onPress={handleConfirm}>
              <Text style={[
                styles.buttonText,
                isDestructive ? styles.destructiveText : styles.confirmText
              ]}>
                {confirmText}
              </Text>
            </Pressable>
          </View>
        </View>
      </View>
    </Modal>
  );
};