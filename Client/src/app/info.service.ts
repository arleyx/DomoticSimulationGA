import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/Rx';

@Injectable()
export class InfoService {

  constructor(public http: HttpClient) { }

  getData(url, component) {
    console.log('URL: ' + url + component + '.json');
    return this.http.get(url + component + '.json')
      .map((res:any) => res.json())
      .catch((error:any) => { console.error(error); return error; });

    // return this.http.get("./file.json")
    
  }

}
