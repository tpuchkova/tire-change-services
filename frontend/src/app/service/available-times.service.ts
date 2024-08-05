import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AvailableTimesService {

  constructor(private _httpClient: HttpClient) { }

  getAvailableTimes(dateFrom: string, dateUntil: string, workshopName: string, carType: string): Observable<AvailableTime[]>{
    if (!dateFrom || !dateUntil){
      dateFrom = "2006-01-02";
      dateUntil = "2100-01-02"
    }
    let params = new HttpParams()

    params = params.append('from', dateFrom)
    params = params.append('until', dateUntil)

    if (workshopName&&workshopName!="all"){
      params = params.append('workshopName', workshopName)
    }
    if (carType&&carType!="all"){
      params = params.append('carType', carType)
    }
    return this._httpClient.get<AvailableTime[]>("http://localhost:9090/getAvailableTimes", {params})
  }
}

export interface AvailableTime {
  id: string
  time: string;
  workshopName: string;
  address: string;
  carType: string;
}
