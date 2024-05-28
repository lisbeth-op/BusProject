using System;

namespace DadoTramposo
{
    internal class Lado
    {
        public int Numero { get; set; }
        public double LimInferior { get; set; }
        public double LimSuperior { get; set; }
        public int VecesSalido { get; set; }

        public Lado(int num, double limInf, double limSup)
        {
            Numero = num;
            LimInferior = limInf;
            LimSuperior = limSup;
            VecesSalido = 0;
        }

        public override string ToString()
        {
            return Numero.ToString();
        }
    }

    internal class Dado
    {
        public Lado[] Lados { get; set; }
        private const double MargenDiferencia = 0.0000000001;

        public Dado()
        {
            Lados = new Lado[]
            {
                new Lado(1, 0, 16.6666666667),
                new Lado(2, 16.6666666668, 33.3333333334),
                new Lado(3, 33.3333333335, 50),
                new Lado(4, 50.0000000001, 66.666666667),
                new Lado(5, 66.666666668, 83.3333333334),
                new Lado(6, 83.3333333335, 100)
            };
        }

        public void CambiarProbabilidad(int numeroDado, int probabilidad)
        {
            double probabilidadRestante = (100 - probabilidad) / 5.0;
            for (int i = 0; i < Lados.Length; i++)
            {
                if (Lados[i].Numero == numeroDado)
                {
                    Lados[i].LimInferior = i == 0 ? 0 : Lados[i - 1].LimSuperior + MargenDiferencia;
                    Lados[i].LimSuperior = Lados[i].LimInferior + probabilidad;
                }
                else
                {
                    Lados[i].LimInferior = i == 0 ? 0 : Lados[i - 1].LimSuperior + MargenDiferencia;
                    Lados[i].LimSuperior = Lados[i].LimInferior + probabilidadRestante;
                }
            }
        }

        private void SimularLanzamiento()
        {
            Random num = new Random();
            var numeroSacado = num.NextDouble() * 100;

            foreach (var lado in Lados)
            {
                if (numeroSacado >= lado.LimInferior && numeroSacado <= lado.LimSuperior)
                {
                    lado.VecesSalido++;
                    Console.WriteLine($"Salió el número {lado.Numero}");
                    break;
                }
            }
        }

        public void SimularMuchosLanzamientos(int veces)
        {
            for (int i = 0; i < veces; i++)
            {
                SimularLanzamiento();
                Task.Delay(200).Wait();
            }
        }

        public void ReiniciarEstadisticas()
        {
            foreach (var lado in Lados)
            {
                lado.VecesSalido = 0;
            }
        }

        public void MostrarEstadisticas()
        {
            Console.WriteLine("ESTADÍSTICAS DEL DADO:");
            foreach (var lado in Lados)
            {
                Console.WriteLine($"Lado {lado.Numero} salió {lado.VecesSalido} veces");
            }
        }
    }
}
