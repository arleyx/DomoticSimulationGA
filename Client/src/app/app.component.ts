import { Component, ViewChild, Renderer2 } from '@angular/core';

import { SocketService } from './socket.service';
import { InfoService } from './info.service'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  private stage: any = {};
  private states: any = {};
  
  private isConnect: boolean = false;
  private isStart: boolean = false;
  private isAlgorithm: boolean = false;
  private urlAlgorithm: string = '';
  private isShowInfo: boolean = false;
  private milliseconds: number = 0;

  @ViewChild('info') infoContainer: any;

  constructor(public socket: SocketService, public info: InfoService, public renderer: Renderer2) {}

  private registerEvents() {
    this.socket.onopen.subscribe((event: Event) => {
      this.isConnect = true;
    });

    this.socket.onmessage.subscribe((event: MessageEvent) => {
      let response = JSON.parse(event.data);

      switch(response.method) {
        case 'connected':
          this.socket.send(JSON.stringify(response));
          break;

        case 'stage':
          console.log(JSON.stringify(response.data));

          this.stage = response.data;
          break;

        case 'timer':
          this.states = response.data.states;
          this.milliseconds = response.data.milliseconds;
          break;

        case 'algorithm':
          this.isAlgorithm = true;
          this.urlAlgorithm = response.data.url;
          break;   
        
        default:
          break;
      }
    });

    this.socket.onerror.subscribe((event: Event) => {
      console.warn('Error en la conexión');
      console.log(event);
    });

    this.socket.onclose.subscribe((event: CloseEvent) => {
      console.warn('Conexión cerrada');
      console.log(event);

      this.isConnect = false;
    });
  }

  showInfo() {
    this.renderer.removeClass(this.infoContainer.nativeElement, 'disable');

    this.info.getData('../../Stages/stage1/case1/results/22-02-2018-15:08:29/components/', 'tv2')
      .subscribe((data) => console.log(data));
  }

  closeInfo() {
    this.renderer.addClass(this.infoContainer.nativeElement, 'disable');
  }

  connect() {
    this.socket.connect();
    this.registerEvents();
  }

  unconnect() {
    this.socket.close();
  }

  start() {
    this.socket.send(JSON.stringify({method: 'start'}));
    this.isStart = true;
  }

  stop() {
    this.socket.send(JSON.stringify({method: 'stop'}));
    this.isStart = false;
  }

  change(component: string, state: string) {
    this.states[component] = state;
    this.socket.send(JSON.stringify({method: 'state', data: this.states}));
  }

  hasActuatorInPlace(place: string) {
    for (let actuator of this.stage.actuators) {
      if (this.states[actuator.name] == place)
        return true;
    }

    return false;
  }

  getClassStyle(component, state) {
    if (this.states[component] != undefined) {
      if (this.states[component] == state)
        return 'onState';
      else if (this.stage.components[component][this.states[component]].indexOf(state) > -1)
        return 'offState';
    }
    return 'disabledState';
  }
}
