import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class SocketService {

  readonly HOST: string = 'ws://localhost:7777';

  private socket: WebSocket;

  public onopen: Observable<any>;
  public onmessage: Observable<any>;
  public onerror: Observable<any>;
  public onclose: Observable<any>;

  constructor() {}

  private registerEvents() {
    this.onopen = new Observable((subscribe) => {
      this.socket.onopen = (event: Event) => subscribe.next(event);
    });

    this.onmessage = new Observable((subscribe) => {
      this.socket.onmessage = (event: MessageEvent) => subscribe.next(event);
    });

    this.onerror = new Observable((subscribe) => {
      this.socket.onerror = (event: Event) => subscribe.next(event);
    });

    this.onclose = new Observable((subscribe) => {
      this.socket.onclose = (event: CloseEvent) => subscribe.next(event);
    });
  }

  connect() {
    this.socket = new WebSocket(this.HOST);
    this.registerEvents();
  }

  send(message) {
    this.socket.send(message);
  }

  close() {
    this.socket.close();
  }
}
