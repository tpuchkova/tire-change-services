import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import moment from "moment/moment";

@Injectable({
  providedIn: 'root'
})
export class AvailableTimesService {

  constructor(private _httpClient: HttpClient) {
  }
  private formatDate(date: string) : string {
    const dateMoment = moment(date, 'MM/DD/YYYY')
    return dateMoment.format("YYYY-MM-DD")
  }

  getAvailableTimes(dateFrom: string, dateUntil: string, workshopName: string, carType: string): Observable<AvailableTime[]> {
    if (!dateFrom) {
      dateFrom = "01/01/2023";
    }
    if (!dateUntil) {
      dateUntil = "01/01/2100";
    }

    let params = new HttpParams()

    params = params.append('from', this.formatDate(dateFrom))
    params = params.append('until', this.formatDate(dateUntil))

    if (workshopName && workshopName != "all") {
      params = params.append('workshopName', workshopName)
    }
    if (carType && carType != "all") {
      params = params.append('carType', carType)
    }
    return this._httpClient.get<AvailableTime[]>("http://localhost:9090/getAvailableTimes", {params})
  }

  sendBookRequest(id: string, contactInformation: string, workshopName: string): Observable<AvailableTime> {
    let params = new HttpParams()
      .set('id', id)
      .set('workshopName', workshopName)

    const requestBody = {
      contactInformation: contactInformation,
    };

    return this._httpClient.post<AvailableTime>(
      "http://localhost:9090/bookAvailableTime",
      requestBody,
      {params})
  }
    getWorkshopsAndCarTypes(): Observable<WorkshopsAndCarTypes> {
      return this._httpClient.get<WorkshopsAndCarTypes>("http://localhost:9090/getWorkshopsAndCarTypes")
    }
}

export interface AvailableTime {
  id: string;
  time: string;
  workshopName: string;
  address: string;
  carType: string;
}

export interface NameValue {
  name: string;
  value: string;
}

export interface WorkshopsAndCarTypes {
  workshops: NameValue[];
  carTypes: NameValue[];
}

