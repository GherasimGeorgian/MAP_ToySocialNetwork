package socialnetwork.controller;

import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

public
class AbonamenteleMeleController {
    private UserServiceFullDB service;
    private Utilizator user_app;
    public void setService(UserServiceFullDB service,Utilizator user) {
        this.service=service;
        this.user_app = user;

    }

}
