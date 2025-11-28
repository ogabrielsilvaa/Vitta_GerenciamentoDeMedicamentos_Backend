import { Asset } from 'expo-asset';
import * as Print from 'expo-print';
import * as Sharing from 'expo-sharing';
import * as FileSystem from 'expo-file-system';
import { Alert } from "react-native";
import { apiManager } from "./apiManager";

const vittaLogoImage = require('../assets/images/logo_vitta.png'); 

export const gerarRelatorioPDF = async (token: string) => {
  try {
    console.log("1. Buscando dados...");
    
    const response = await apiManager.get('/api/medicamentoHistoricos/relatorio-mensal-dados', {
      headers: { Authorization: `Bearer ${token}` }
    });

    const dados = response.data;

    if (!dados || dados.length === 0) {
      Alert.alert("Aviso", "Não há registros para gerar o relatório deste mês.");
      return;
    }

    console.log("2. Processando imagem...");
    let logoSrcBase64 = null;

    try {
        const asset = Asset.fromModule(vittaLogoImage);
        
        await asset.downloadAsync();

        if (asset.localUri) {
            const base64Data = await FileSystem.readAsStringAsync(asset.localUri, {
                encoding: 'base64' 
            });
            logoSrcBase64 = `data:image/png;base64,${base64Data}`;
        }
    } catch (imgError) {
        console.warn("Erro ao processar imagem do logo:", imgError);
    }

    console.log("3. Gerando HTML...");
    
    const htmlContent = criarHtml(dados, logoSrcBase64);

    console.log("4. Gerando PDF...");

    const { uri } = await Print.printToFileAsync({
      html: htmlContent,
      base64: false
    });

    console.log("PDF salvo em:", uri);

    await Sharing.shareAsync(uri, { 
        UTI: 'com.adobe.pdf', 
        mimeType: 'application/pdf', 
        dialogTitle: 'Relatório Vitta' 
    });

  } catch (error) {
    console.error("Erro PDF:", error);
    Alert.alert("Erro", "Falha ao gerar PDF.");
  }
};

const criarHtml = (dados: any[], logoSrc: string | null) => {
  const dataAtual = new Date();
  const nomeMes = dataAtual.toLocaleString("pt-BR", { month: "long" });
  const mesFormatado = nomeMes.charAt(0).toUpperCase() + nomeMes.slice(1);
  const ano = dataAtual.getFullYear();
  
  const tituloRelatorio = `Relatório de Medicamentos - ${mesFormatado}/${ano}`;
  const hojeString = dataAtual.toLocaleDateString("pt-BR");
  
  const linhas = dados.map((item: any) => {
    const trat = item.nomeTratamento || "-";
    const med = item.nomeMedicamento || "-";
    
    const dataObj = new Date(item.horaDoUso);
    dataObj.setHours(dataObj.getHours() - 3);
    
    const data = dataObj.toLocaleString("pt-BR", { 
        day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' 
    });
    
    const dose = item.doseTomada;
    const obs = item.observacao || "";

    return `
      <tr style="border-bottom: 1px solid #eee;">
        <td style="padding: 8px;">${trat}</td>
        <td style="padding: 8px;">${med}</td>
        <td style="padding: 8px; text-align: center;">${dose}</td>
        <td style="padding: 8px; text-align: center;">${data}</td>
        <td style="padding: 8px; font-size: 10px; color: #666;">${obs}</td>
      </tr>
    `;
  }).join('');

  const logoHtml = logoSrc 
    ? `<div class="logo-container"><img src="${logoSrc}" class="logo-img" alt="Vitta Logo" /></div>` 
    : '';

  return `
    <html>
      <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no" />
        <style>
          body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; padding: 30px 20px; }
          
          .logo-container { text-align: center; margin-bottom: 20px; }
          .logo-img { height: 60px; width: auto; object-fit: contain; }

          h1 { color: #333; text-align: center; margin-bottom: 5px; font-size: 22px; }
          p.data { text-align: center; color: #888; margin-bottom: 30px; font-size: 12px; }
          
          table { width: 100%; border-collapse: collapse; margin-top: 20px; }
          th { background-color: #1CBDCF; color: white; padding: 10px; text-align: left; font-size: 12px; }
          td { font-size: 11px; }
          tr:nth-child(even) { background-color: #f9f9f9; }
        </style>
      </head>
      <body>
        
        ${logoHtml}
        
        <h1>${tituloRelatorio}</h1>
        <p class="data">Gerado em: ${hojeString}</p>
        
        <table>
          <thead>
            <tr>
              <th>Tratamento</th>
              <th>Medicamento</th>
              <th style="text-align: center;">Dose</th>
              <th style="text-align: center;">Data</th>
              <th>Obs.</th>
            </tr>
          </thead>
          <tbody>
            ${linhas}
          </tbody>
        </table>
      </body>
    </html>
  `;
};