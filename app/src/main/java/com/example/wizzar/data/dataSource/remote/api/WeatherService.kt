package com.example.wizzar.data.dataSource.remote.api

class WeatherService {
    //http://api.openweathermap.org/geo/1.0/direct?q=London&limit=5&appid=e8d7d515e8dc544a8ebcb0c3e0eec3dc -> get gecode based on city name
    // http://api.openweathermap.org/geo/1.0/reverse?lat={lat}&lon={lon}&limit={limit}&appid=e8d7d515e8dc544a8ebcb0c3e0eec3dc -> get city name based on gecode
    //http://api.openweathermap.org/data/2.5/forecast?lat=33.94&lon=-94.04&appid=e8d7d515e8dc544a8ebcb0c3e0eec3dc-> get 5 day weather forecast based on gecode
    //http://api.openweathermap.org/data/2.5/weather?q=London&appid=e8d7d515e8dc544a8ebcb0c3e0eec3dc -> get current weather based on city name
    //http://api.openweathermap.org/data/2.5/weather?lat=33.94&lon=-94.04&appid=e8d7d515e8dc544a8ebcb0c3e0eec3dc -> get current weather based on gecode
}