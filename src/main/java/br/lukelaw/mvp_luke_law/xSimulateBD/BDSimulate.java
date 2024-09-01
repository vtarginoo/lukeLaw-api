package br.lukelaw.mvp_luke_law.xSimulateBD;


import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BDSimulate {

    // Simulando o banco de dados de usuários cadastrados
    public List<User> usuarios;

    // Simulando a associação de processos aos IDs dos usuários
    public Map<String, List<Integer>> processosAssociados;

    // Simulando a associação de processos aos IDs dos usuários
    public Map<String, List<Integer>> processosAssociadosTSE;


    public BDSimulate() {
        this.usuarios = new ArrayList<>();
        this.processosAssociados = new HashMap<>();
        this.processosAssociadosTSE = new HashMap<>();

        // Mockando alguns usuários
        usuarios.add(new User(1, "Youssef Y.", "whatsapp:+5521996066505"));
        usuarios.add(new User(2, "Victor M.", "whatsapp:+5521996800927"));
        usuarios.add(new User(3, "Andréa M.", "whatsapp:+5521972802709"));

        // Mockando algumas associações de processos aos IDs dos usuários
        processosAssociados.put("0809129-51.2024.8.19.0001", List.of(1,2));
        processosAssociados.put("0838717-06.2024.8.19.0001", List.of(1,2));
        processosAssociados.put("0907787-47.2023.8.19.0001", List.of(1, 2));
        processosAssociados.put("0947617-20.2023.8.19.0001", List.of(1, 2));
        processosAssociados.put("0938160-61.2023.8.19.0001", List.of(1, 2));


        processosAssociadosTSE.put("0600316-53.2024.6.19.0076", List.of(2));
        processosAssociadosTSE.put("0600158-92.2024.6.19.0174", List.of(2));
        processosAssociadosTSE.put("0600244-47.2024.6.19.0050", List.of(2));
    }

     // Método para Identificar o Usuário por meio do Celular
    public User findUserByPhoneNumber(String celular) {
        return usuarios.stream()
                .filter(user -> user.celular().equals(celular))
                .findFirst()
                .orElse(null);
    }

}
