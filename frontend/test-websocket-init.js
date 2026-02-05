// Cole este código no console do navegador para testar

console.log('🔍 Testando inicialização do WebSocket...');

// Tenta importar e inicializar manualmente
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost/ws'),
  debug: (str) => console.log('STOMP:', str),
  onConnect: () => {
    console.log('✅ CONECTADO!');
    client.subscribe('/topic/albums', (msg) => {
      console.log('📩 Mensagem:', msg.body);
    });
  }
});

console.log('Ativando cliente...');
client.activate();
