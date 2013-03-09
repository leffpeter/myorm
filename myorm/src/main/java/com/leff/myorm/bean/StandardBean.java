/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.leff.myorm.bean;

import com.leff.myorm.bean.annotation.BeanColumn;
import com.leff.myorm.constant.DataModel;

import java.util.Date;

/**
 * Bean estandar del modelo.
 *
 * @author leff
 */
public abstract class StandardBean extends BaseBean {

    /**
     * Timestamp.
     */
    @BeanColumn(DataModel.C_OBS)
    private String obs;
    @BeanColumn(DataModel.C_FEC_ALTA)
    private Date fecAlta;
    @BeanColumn(DataModel.C_FEC_BAJA)
    private Date fecBaja;

    /**
     * constructor.
     */
    public StandardBean() {
        super();
    }

    /**
     * constructor
     *
     * @param bean Bean
     */
    public StandardBean(Bean bean) {
        super(bean);
    }

    /**
     * Constructor protegido.
     *
     * @param validateKeysOnSet flag para activar/desactivar el chequedo de keys
     *                          válidas durante el setKey.
     */
    protected StandardBean(boolean validateKeysOnSet) {
        super(validateKeysOnSet);
    }

    /**
     * @return the obs
     */
    public final String getObs() {
        return obs;
    }

    /**
     * @param obs the obs to set
     */
    public final void setObs(String obs) {
        this.obs = obs;
    }

    /**
     * @return the fecAlta
     */
    public final Date getFecAlta() {
        return (Date) (fecAlta != null ? fecAlta.clone() : null);
    }

    /**
     * @param fecAlta the fecAlta to set
     */
    public final void setFecAlta(Date fecAlta) {
        this.fecAlta = (Date) (fecAlta != null ? fecAlta.clone() : null);
    }

    /**
     * @return the fecBaja
     */
    public final Date getFecBaja() {
        return (Date) (fecBaja != null ? fecBaja.clone() : null);
    }

    /**
     * @param fecBaja the fecBaja to set
     */
    public final void setFecBaja(Date fecBaja) {
        this.fecBaja = (Date) (fecBaja != null ? fecBaja.clone() : null);
    }
}
