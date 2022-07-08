[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-c66648af7eb3fe8bc4f294546bfd86ef473780cde1dea487d3c4ff354943c9ae.svg)](https://classroom.github.com/online_ide?assignment_repo_id=7992130&assignment_repo_type=AssignmentRepo)
# Trabalho avaliativo de MATA59 - Redes de Computadores
### Álvaro Oliveira, Caio Santos, Carlos Neto e Vanessa Araújo.


## CAMADA DE APLICAÇÃO: 

Contamos com duas aplicações: uma para o cliente e outra para o servidor. A classe FileClient  e a classe FileServer respectivamente. Ambas aplicações não terão interface gráfica.  

No servidor terá um critério de aceitação da conexão iniciada pelo cliente. 

Nessa camada, o arquivo e suas informações serão empacotadas e repassadas à camada de transporte, que fará seu envio.  

Por conta do envio e recebimento de arquivos entre a rede, através do Client e do Server, temos a simulação de um protocolo FTP, que tem por função fazer o envio e recebimento de arquivos entre duas aplicações. 

## CAMADA DE TRANSPORTE: 

Simulando a camada de transporte, a qual deve realizar o transporte de dados entre sistemas finais, temos na classe FileServer, a criação do socket passando IP de destino e porta de entrada, há uma tentativa de conexão, ou seja, é responsável por fazer a ligação entre a camada de aplicação e a camada de rede (responsável por aceitar ou não essa conexão. 

Na nossa aplicação fazemos o uso do protocolo de transporte FTP pelo fato de nossa aplicação fazer transporte de arquivos de texto, os quais não podemos tolerar perdas, pois desejamos que este tipo de arquivo não sofra perdas neste processo. Este protocolo está encapsulado nas bibliotecas usadas pelo Socket. 

## CAMADA DE REDE: 

Sabendo que a camada de rede é responsável por roteamento e repasse de dados além de estabelecimento de conexão, simulamos a camada de rede utilizando as bibliotecas JAVA java.net.socket e java.net.ServerSocket. A partir delas utilizamos o objeto ServerSocket que tem como parâmetro a porta a ser utilizada, definida ao executar o servidor. Como o próprio nome já intui, esta é responsável por aceitar ou recusar as conexões propostas pela aplicação cliente. 

O cliente, então, pode utilizar a classe de socket, junto da porta e o IP de destino, permitindo uma comunicação bidirecional entre processos. Ou seja, vai existir um socket datagrama contendo endereço de IP e porta de destino final, utilizando funções da própria biblioteca citada, para enviar e receber esses pacotes para e das camadas inferiores. 

## CAMADA DE ENLACE: 

A camada de enlace fica encapsulada com a camada física e isso tudo é simulado através do uso dos Sockets que nossa aplicação utiliza. 


## CAMADA FÍSICA: 

A camada física será simulada através de sockets, utilizando as bibliotecas JAVA <java.net.socket> e <java.net.ServerSocket> com datagramas. Esse método, bem como a camada física real, não garante confiabilidade dos dados. 

## Rodando a aplicação: 

Para rodar a aplicação, primeiro deve-se executar a classe FileServer e selecionar uma porta de sua preferência. Feito isso, o servidor irá está pronto para receber conexões e ficará no estado aguardando.

Agora, pode-se executar a classe FileClient. Nela, deve ser passado o ip do servidor (Ex: O IPV4 do Adaptador Ethernet), a porta utilizada na inicialização do FileServer e o nome do cliente. A partir daí, se possível, a conexão vai ser feita com o servidor e vai ser paresentado na tela todos os arquivos, caso haja, do cliente com o nome passado. A partir desse momento, 4 opções podem ser escolhidas correspondente a cada funcionalidade da aplicação:
1. Upload de um arquivo.
2. Download de um arquivo.
3. Remoção de um arquivo.
4. Alterar nível de tolerância a falhas.

## OBSERVAÇÕES: 

Nosso programa é sensível a falhas, porém, qualquer problema que houver com o arquivo e o carregamento do mesmo em memória, a JVM detectará uma “exception”, o que garantirá que o problema irá ser detectado no log do programa. 
