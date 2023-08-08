package com.cars.enums;

import com.cars.enums.CarModelType.Ford;
import com.cars.enums.CarModelType.Toyota;
import com.cars.enums.CarModelType.Vaz;
import com.cars.enums.CarModelType.Volkswagen;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Data
@Deprecated
public class Model {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Ford ford;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Toyota toyota;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Volkswagen volkswagen;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Vaz vaz;

    @Override
    public String toString() {
        return "Model{" +
                "ford=" +
                    "{" +
                        Ford.FOCUS.name() + "," +
                        Ford.FIESTA.name() + "," +
                        Ford.FUSION.name() +
                    "}" +
                ", toyota=" +
                    "{" +
                        Toyota.CAMRY.name() + "," +
                        Toyota.COROLLA.name() + "," +
                        Toyota.PRIUS.name() +
                    "}" +
                ", volkswagen=" +
                    "{" +
                        Volkswagen.JETTA.name() + "," +
                        Volkswagen.PASSAT.name() + "," +
                        Volkswagen.TUAREG.name() +
                    "}" +
                ", vaz=" +
                    "{" +
                        Vaz.NIVA.name() + "," +
                        Vaz.VOLGA.name() + "," +
                        Vaz.RIVA.name() +
                    "}" +
                '}';
    }
}
